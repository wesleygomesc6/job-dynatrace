package br.com.monitoramento.jobdynatrace.serivces;

import br.com.monitoramento.jobdynatrace.models.Entitie;
import br.com.monitoramento.jobdynatrace.models.Problem;
import br.com.monitoramento.jobdynatrace.models.response.Detail;
import br.com.monitoramento.jobdynatrace.models.response.EntitieResponse;
import br.com.monitoramento.jobdynatrace.models.response.ProblemResponse;
import br.com.monitoramento.jobdynatrace.models.response.Propertie;
import br.com.monitoramento.jobdynatrace.repository.EntitieRepository;
import br.com.monitoramento.jobdynatrace.repository.ProblemRepository;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateEndTimeAndStatusService {
	
	@Autowired
	ProblemRepository problemRepository;

	@Autowired
	EntitieRepository entitieRepository;
	
	@Value("${spring.dynatrace.urlv2}")
	private String urlv2;
	
	@Value("${spring.dynatrace.token}")
	private String token;
	
    private final long MINUTO = 1000 * 60;
    
    private static Logger logger = (Logger) LoggerFactory.getLogger(UpdateEndTimeAndStatusService.class);
	
    @Scheduled(fixedDelay = MINUTO * 15)
    @Transactional
    public void updateProblems() {
    	logger.info("===================================================================");
    	logger.info("Verificando se existe problemas em aberto...");
    	
    	List<Problem> probOpens = problemRepository.findByStatus("OPEN");
		if(probOpens.isEmpty()) {
			logger.info("Sem problemas abertos no momento.");
		} else {
			logger.info(probOpens.size() + " problemas em aberto.");
			logger.info("Iniciando processo de atualização.");
			for(Problem p : probOpens) {
				String url = urlv2+"problems/"+p.getProblemId()+"?api-token="+token;
				ProblemResponse problema = new RestTemplate().getForObject(url, ProblemResponse.class);
				Problem atualizar = problemRepository.getById(p.getId());
				if(problema.getStatus() == "OPEN") {
					logger.info("Este problema continua em aberto no dynatrace. Problem Id = " + problema.getProblemId());

				} else {
					atualizar.setStatus(problema.getStatus());
					atualizar.setEndTime(problema.getEndTime());
					if(problema.getRootCauseEntity() != null) {
						Entitie rootCause = entitieRepository.findByEntityId(problema.getRootCauseEntity().getEntityId().getId());
						atualizar.setRootCauseEntity(rootCause);
					}

					if(problema.getAffectedEntities() != null) {
						List<Entitie> affecteds = new ArrayList<>();

						for(EntitieResponse affected : problema.getAffectedEntities()) {

							Entitie afetada = entitieRepository.findByEntityId(affected.getEntityId().getId());
							affecteds.add(afetada);

						}

						atualizar.setAffectedEntities(affecteds);

					}

					if(problema.getImpactedEntities() != null) {
						List<Entitie> impacteds = new ArrayList<>();

						for(EntitieResponse impacted : problema.getImpactedEntities()) {
							Entitie impactada = entitieRepository.findByEntityId(impacted.getEntityId().getId());
							impacteds.add(impactada);

						}
						atualizar.setImpactedEntities(impacteds);

					}

					if(problema.getEvidenceDetails() != null) {
						List<Entitie> evidencias = new ArrayList<>();
						for(Detail detalhe : problema.getEvidenceDetails().getDetails()) {
							if(detalhe.getData() != null) {
								for(Propertie propriedade : detalhe.getData().getProperties()) {
									if(propriedade.getValue().contains("SERVICE_")) {
										Entitie evidencia = entitieRepository.findByEntityId(propriedade.getValue());
										if(!evidencias.contains(evidencia)) {
											evidencias.add(evidencia);
										}

									}
								}
							} else {
								logger.info("Esta evidencia nao possui o campo data.");

							}
						}
						atualizar.setEvidences(evidencias);
						logger.info("Evidencia(s) salva(s).");
					} else {
						logger.info("Este problema nao possui evidencias.");

					}


					problemRepository.save(atualizar);
					logger.info("Problema atualizado. Problem Id = " + problema.getProblemId());
				}
				
			}
			
		}
		logger.info("Fim da atualização.");
    }

}
