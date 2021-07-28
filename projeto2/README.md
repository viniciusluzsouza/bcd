# STD - Projeto Pratico 2
  
**Aluno:** Vinicius da Luz Souza
  
  
  
**Para compilar:**  
Logger -> *javac logger/Executer.java*  
Workers -> *javac worker/Executer.java*  

**Para executar:**  
Logger -> *java -Djava.security.policy=java.policy logger.Executer*  
Workers -> *java -Djava.security.policy=java.policy worker.Executer pID Semente tempoEspera tempoJitter arquivo-com-processos arquivo-com-eventos*  

Exemplo: *java -Djava.security.policy=java.policy worker.Executer p1 1234 1000 2000 process.txt eventos-p1.txt*
