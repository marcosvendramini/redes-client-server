import java.net.ServerSocket;

/**
 * Classe utilizada para iniciar um Socket Servidor
 *
 * @author Arthur Floresta Rezende
 * @author Marcos Felipe Vendramini Carvalho
 */
public class Servidor{

  /**
   * Metodo principal
   *
   * @param args argumentos do programa
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    ServerSocket listener = new ServerSocket(12345);
    System.out.println("Esperando clientes...");

    try{
      while (true){
        // Criado um novo jogo
        Jogo jogo = new Jogo();

        // Instancia os jogadores (aceita a conexao)
        Jogo.Jogador jogador1 = jogo.new Jogador(listener.accept(), 1);
        Jogo.Jogador jogador2 = jogo.new Jogador(listener.accept(), 2);

        // Instancia os adversarios
        jogador1.setOponente(jogador2);
        jogador2.setOponente(jogador1);

        // Comeca o jogo
        jogador1.start();
        jogador2.start();
      }
    }finally{
      listener.close();
    }
  }
}
