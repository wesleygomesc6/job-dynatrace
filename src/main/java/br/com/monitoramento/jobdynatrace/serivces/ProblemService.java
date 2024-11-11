package br.com.monitoramento.jobdynatrace.serivces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.monitoramento.jobdynatrace.models.Comment;
import br.com.monitoramento.jobdynatrace.models.Entitie;
import br.com.monitoramento.jobdynatrace.models.EntityType;
import br.com.monitoramento.jobdynatrace.models.ImpactLevelType;
import br.com.monitoramento.jobdynatrace.models.Problem;
import br.com.monitoramento.jobdynatrace.models.response.CommentResponse;
import br.com.monitoramento.jobdynatrace.models.response.Detail;
import br.com.monitoramento.jobdynatrace.models.response.EntitieResPage;
import br.com.monitoramento.jobdynatrace.models.response.EntitieResponse;
import br.com.monitoramento.jobdynatrace.models.response.EntitieResponseSelector;
import br.com.monitoramento.jobdynatrace.models.response.ModelCommentResponse;
import br.com.monitoramento.jobdynatrace.models.response.ModelProblemResponse;
import br.com.monitoramento.jobdynatrace.models.response.ProblemResponse;
import br.com.monitoramento.jobdynatrace.models.response.Propertie;
import br.com.monitoramento.jobdynatrace.repository.CommentRepository;
import br.com.monitoramento.jobdynatrace.repository.EntitieRepository;
import br.com.monitoramento.jobdynatrace.repository.EntityTypeRepository;
import br.com.monitoramento.jobdynatrace.repository.ImpactLevelTypeRepository;
import br.com.monitoramento.jobdynatrace.repository.ProblemRepository;

@Service
public class ProblemService {

	@Autowired
	ProblemRepository problemRepository;

	@Autowired
	EntityTypeRepository entityTypeRepository;

	@Autowired
	EntitieRepository entitieRepository;

	@Autowired
	ImpactLevelTypeRepository impactLevelTypeRepository;

	@Autowired
	CommentRepository commentRepository;

	@Value("${spring.dynatrace.urlv2}")
	private String urlv2;

	@Value("${spring.dynatrace.token}")
	private String token;

	private final long MINUTO = 1000 * 60;

	private static  List<String> databases = Arrays.asList("sf01", "SF01", "SF04", "hmlg01", "alfresco", "<default>");

	private static Logger logger = (Logger) LoggerFactory.getLogger(ProblemService.class);


	@Scheduled(fixedDelay = MINUTO * 5)
	@Transactional
	public void getProblems() {
		String ultimoProblema = problemRepository.findByLastProblems();
		String from = ultimoProblema.substring(0, ultimoProblema.length() - 2);

		String url = urlv2+"problems?from="+from+"-03:00&to=now&sort=startTime&pageSize=500&api-token="+token;

		logger.info("*********************************************************************************");
		logger.info("Iniciando requisicao dos problemas a partir de: " + from);

		//String url = urlv2+"problems?from=2022-10-02 00:00:00-03:00&to=2022-10-13 23:59:00-03:00&sort=startTime&pageSize=500&api-token="+token;

		ModelProblemResponse problemas =  new RestTemplate().getForObject(url, ModelProblemResponse.class);

		logger.info(problemas.getProblems().size() + " problemas encotrados.");

		if(problemas.getProblems().isEmpty()) {
			logger.info("Nenhum problema encontrado.");

		} else {
			logger.info("Iniciando o Processo de salvamento.");

			for(ProblemResponse problema : problemas.getProblems() ) {
				logger.info("Verificando... Problem Id = " + problema.getProblemId());

				Optional<Problem> exist = problemRepository.findByDisplayId(problema.getDisplayId());

				if(exist.isPresent()) {
					logger.info("Este problema ja existe.");

				} else {
					ImpactLevelType il = impactLevelTypeRepository.findByName(problema.getImpactLevel());
					if(il == null ) {
						ImpactLevelType impLevel = new ImpactLevelType();
						impLevel.setName(problema.getImpactLevel());
						impactLevelTypeRepository.save(impLevel);
					}

					List<EntitieResponse> affectedAndImpactedAndRoot = new ArrayList<>();
					affectedAndImpactedAndRoot.add(problema.getRootCauseEntity());
					affectedAndImpactedAndRoot.addAll(problema.getAffectedEntities());
					affectedAndImpactedAndRoot.addAll(problema.getImpactedEntities());

					for(EntitieResponse entitie : affectedAndImpactedAndRoot) {
						if(entitie != null && entitieRepository.findByEntityId(entitie.getEntityId().getId()) == null) {

							EntityType tipo = entityTypeRepository.findByName(entitie.getEntityId().getType());
							if(tipo == null) {
								EntityType type = new EntityType();
								type.setName(entitie.getEntityId().getType());
								entityTypeRepository.save(type);
							}
							Entitie entidade = new Entitie();
							entidade.setEntityId(entitie.getEntityId().getId());
							entidade.setName(entitie.getName());

							if(databases.contains(entitie.getName())) {
								ImpactLevelType eltd = impactLevelTypeRepository.findByName("DATABASE");
								if(eltd == null) {
									ImpactLevelType n = new ImpactLevelType();
									n.setName("DATABASE");
									impactLevelTypeRepository.save(n);
								}
								EntityType etDatabase = entityTypeRepository.findByName("DATABASE");
								if(etDatabase == null) {
									EntityType ed = new EntityType();
									ed.setName("DATABASE");
									entityTypeRepository.save(ed);
								}
								EntityType tipoEntidadeDatabase = entityTypeRepository.findByName("DATABASE");
								entidade.setEntityType(tipoEntidadeDatabase);
							} else {
								EntityType tipoEntidade = entityTypeRepository.findByName(entitie.getEntityId().getType());
								entidade.setEntityType(tipoEntidade);

							}

							entitieRepository.save(entidade);
						}
					}

					// Passa o problema para o método que vai salva-lo.

					setProblema(problema);

				}
			}
		}

		logger.info("Fim do processamento.");

	}

	@Transactional
	public void setProblema(ProblemResponse problema) {

		logger.info("Salvando problema...");

		Problem pr = new Problem();
		pr.setProblemId(problema.getProblemId());
		pr.setDisplayId(problema.getDisplayId());
		pr.setTitle(problema.getTitle());
		pr.setStartTime(problema.getStartTime());
		pr.setEndTime(problema.getEndTime());
		pr.setEndTime(problema.getEndTime());

		pr.setSeverityLevel(problema.getSeverityLevel());
		pr.setStatus(problema.getStatus());
		pr.setValidated(false);

		if(problema.getRootCauseEntity() != null) {
			Entitie rootCauseExist = entitieRepository.findByEntityId(problema.getRootCauseEntity().getEntityId().getId());
			pr.setRootCauseEntity(rootCauseExist);
		}

		if(problema.getAffectedEntities() != null) {
			List<Entitie> affecteds = new ArrayList<>();

			for(EntitieResponse affected : problema.getAffectedEntities()) {

				Entitie afetada = entitieRepository.findByEntityId(affected.getEntityId().getId());
				affecteds.add(afetada);

			}

			pr.setAffectedEntities(affecteds);

		}


		if(problema.getImpactedEntities() != null) {
			List<Entitie> impacteds = new ArrayList<>();
			List<ImpactLevelType> impactLevels = new ArrayList<>();

			for(EntitieResponse impacted : problema.getImpactedEntities()) {

				ImpactLevelType impactLevelExist = impactLevelTypeRepository.findByName(impacted.getEntityId().getType());

				if(impactLevelExist == null) {
					ImpactLevelType impactLevel = new ImpactLevelType();
					impactLevel.setName(impacted.getEntityId().getType());
					impactLevelTypeRepository.save(impactLevel);
				}

				ImpactLevelType il = impactLevelTypeRepository.findByName(problema.getImpactLevel());

				// Verifica se o problema impactou banco de dados.
				if (databases.contains(impacted.getName())) {

					ImpactLevelType impactLevelDatabase = impactLevelTypeRepository.findByName("DATABASE");
					impactLevels.add(impactLevelDatabase);

				} else if(!impactLevels.contains(il)){
					impactLevels.add(il);
				}

				if(problema.getImpactedEntities().size() > 1) {
					pr.setImpactLevel(problema.getImpactLevel());
				} else if(databases.contains(impacted.getName())){
					pr.setImpactLevel("DATABASE");
				} else {
					pr.setImpactLevel(problema.getImpactLevel());
				}

				Entitie impactada = entitieRepository.findByEntityId(impacted.getEntityId().getId());
				impacteds.add(impactada);

			}
			pr.setImpactLevels(impactLevels);
			pr.setImpactedEntities(impacteds);

		}

		problemRepository.save(pr);
		logger.info("Problema salvo.");

		setComment(pr.getProblemId());
		setEvidences(pr.getProblemId());
	}

	@Transactional
	public void setComment(String problemId) {
		logger.info("----------------------------------------------");
		logger.info("Verificando se o problema possui comentarios.");

		String url = urlv2+"problems/"+problemId+"/comments?api-token="+token;

		ModelCommentResponse comments =  new RestTemplate().getForObject(url, ModelCommentResponse.class);

		Problem problema = problemRepository.findByProblemId(problemId);

		if(comments.getComments().isEmpty()) {
			logger.info("Este problema nao possui comentarios.");
		} else {
			logger.info("Salvando comentario(s)...");
			for(CommentResponse comentario : comments.getComments()) {
				Comment comment = new Comment();
				comment.setAuthorName(comentario.getAuthorName());
				comment.setContent(comentario.getContent());
				comment.setProblem(problema);

				commentRepository.save(comment);
				logger.info("Comentario(s) salvo(s).");
			}

		}

	}

	@Transactional
	public void setEvidences(String problemId) {
		logger.info("..............................................");
		logger.info("Verificando evidencias do problema.");

		String urlEvidence = urlv2+"problems/"+problemId+"?api-token="+token;

		ProblemResponse problema = new RestTemplate().getForObject(urlEvidence, ProblemResponse.class);

		/* Separa os ids das evidencias e adiciona em um array para poder salvar no método saveEntitie
		 * Os ids ficam no campo: 'value' dentro de problema > evidenceDetails > details > data > properties. */

		if(problema.getEvidenceDetails() != null) {
			List<String> idsEntitidades = new ArrayList<>();
			for(Detail detalhe : problema.getEvidenceDetails().getDetails()) {
				if(detalhe.getData() != null) {
					for(Propertie propriedade : detalhe.getData().getProperties()) {
						if(propriedade.getValue().contains("SERVICE_")) {
							if(!idsEntitidades.contains(propriedade.getValue())) {
								idsEntitidades.add(propriedade.getValue());
							}
						}
					}
				} else {
					logger.info("Esta evidencia nao possui o campo data.");

				}
			}
			saveEntitie(idsEntitidades, problema.getProblemId());

		} else {
			logger.info("Este problema nao possui evidencias.");

		}
	}

	@Transactional
	public void saveEntitie(List<String> entityIds, String problemId) {
		logger.info("Salvando evidencias...");

		List<Entitie> evidencias = new ArrayList<>();
		Problem problema = problemRepository.findByProblemId(problemId);

		for(String entityId : entityIds) {
			String url = urlv2+"entities?entitySelector=entityId(\""+entityId+"\")&api-token="+token;

			Entitie entitieExist = entitieRepository.findByEntityId(entityId);

			if(entitieExist == null) {
				EntitieResPage entidade =  new RestTemplate().getForObject(url, EntitieResPage.class);

				for(EntitieResponseSelector e : entidade.getEntities()) {

					Entitie eSalvar = new Entitie();
					eSalvar.setEntityId(e.getEntityId());
					eSalvar.setName(e.getDisplayName());

					EntityType tipo = entityTypeRepository.findByName(e.getType());
					if(tipo == null) {
						EntityType type = new EntityType();
						type.setName(e.getType());
						entityTypeRepository.save(type);
					}

					EntityType t = entityTypeRepository.findByName(e.getType());
					eSalvar.setEntityType(t);

					entitieRepository.save(eSalvar);
				}
			}
			Entitie evid = entitieRepository.findByEntityId(entityId);
			evidencias.add(evid);
			problema.setEvidences(evidencias);

			problemRepository.save(problema);
			logger.info("Evidencia(s) salva(s).");
		}
		logger.info("..............................................");
	}
}
