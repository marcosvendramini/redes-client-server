import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe utilizada para controlar o fluxo do jogo
 *
 * @author Arthur Floresta Rezende
 * @author Marcos Felipe Vendramini Carvalho
 */
class Jogo {
  private int jogada1, jogada2;

  /**
   * Construtor da classe
   */
  public Jogo() {
    jogada1 = -1;
    jogada2 = -1;
  }

  /**
   * Metodo que checka o ganhador do jogo onde:
   *    Jogadas: 0 -> Pedra, 1 -> Papel, 2 -> Tesoura
   *    Vitoria: 0 -> Empate, 1 -> player1, 2 -> player2
   */
  public int vencedor(){
    if(jogada1 < 0 || jogada2 < 0) return -1; // ERRO

    if(jogada1 == jogada2) return 0;

    if(jogada1 == 0 && jogada2 == 1) return 2;
    if(jogada1 == 0 && jogada2 == 2) return 1;

    if(jogada1 == 1 && jogada2 == 0) return 1;
    if(jogada1 == 1 && jogada2 == 2) return 2;

    if(jogada1 == 2 && jogada2 == 0) return 2;
    if(jogada1 == 2 && jogada2 == 1) return 1;

    return -1; // ERRO
  }

  /**
   * Metodo para executar jogada
   *
   * @param jogada codido da jogada1
   * @param jogadorNum codigo do jogador
   */
  public synchronized void executaJogada(int jogada, int jogadorNum) {
    if(jogadorNum == 1) jogada1 = jogada;
    if(jogadorNum == 2) jogada2 = jogada;
  }

  /**
   * Classe interna usada para controlar o jogador
   * usando uma thread specifica para o mesmo
   */
  class Jogador extends Thread {
    private int jogNum;
    private Jogador oponente;
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter saida;

    /**
     * Construtor da classe
     *
     * @param socket socket do jogador
     * @param jogNum numero de referencia do jogador
     */
    public Jogador(Socket socket, int jogNum) {
      //Recebe o socket em que foi conectado
      this.socket = socket;
      this.jogNum = jogNum;

      try{
        // Instancia as referencias de entrada e saida de dados
        // so socket cliente
        entrada = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
        saida = new PrintWriter(socket.getOutputStream(), true);

        // Envia mensagem inicial de conexao para o cliente
        enviaMSG("BEM VINDO " + jogNum);
        enviaMSG("MENSAGEM Esperando pelo adversário...");

      } catch (IOException e){
        System.out.println("Jogador desconectou: " + e);
      }
    }

    /**
     * Metodo que instancia a referencia ao oponente
     *
     * @param oponente instancia do oponente
     */
    public void setOponente(Jogador oponente) {
      this.oponente = oponente;
    }

    /**
     * Metodo para enviar mensagem
     *
     * @param msg mensagem a ser enviada
     */
    public void enviaMSG(String msg) {
      saida.println(msg);
    }

    /**
     * Metodo da Thread que contem o loop a ser executado
     */
    public void run(){
      try {
        enviaMSG("MENSAGEM Jogadores Conectados");

        while(true) {
          String comando = entrada.readLine();

          if(comando != null && comando.startsWith("JOGADA")) {
            int jog = Integer.parseInt(comando.substring(7));

            // executa a jogada (secao critica)
            executaJogada(jog, this.jogNum);
            int vence = vencedor();

            // Checka codigos de vitoria e "reage" a eles
            if(vence != -1) {
              if(vence == 0) {
                enviaMSG("EMPATE");
                oponente.enviaMSG("EMPATE");
              }
              else if(vence == jogNum) {
                enviaMSG("VITORIA");
                oponente.enviaMSG("DERROTA");
              }
              else {
                enviaMSG("DERROTA");
                oponente.enviaMSG("VITORIA");
              }
            }
          }
        }
      } catch(IOException e) { System.out.println("Jogador desconectou.");
      } finally { // fechando o socket
        try {
          socket.close();
        } catch(IOException e) { }
      }
    }
  }
}
