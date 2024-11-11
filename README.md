# job-dynatrace

### Objetivo
Buscar os problemas registrados pelo Dynatrace e salvar em banco de dados local para uma melhor acertividade das informações e geração de estatíscas em relação aos problemas.

### Execução
A cada 01 (uma) hora o job faz a requisição dos problemas a partir da data e hora do ultimo problema salvo no banco.

A cada 06 (seis) horas o job faz uma consulta ao banco para verificar se existe algum problema com `status` em aberto e atualiza o status e o endTime do problema.

