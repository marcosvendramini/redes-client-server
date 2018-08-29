import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Classe utilizada para iniciar um Socket Cliente
 *
 * @author Arthur Floresta Rezende
 * @author Marcos Felipe Vendramini Carvalho
 */
public class Cliente{
    private JFrame frame;
    private JLabel mensagem;
    private JButton b1,b2,b3;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter	saida;

    /**
     * Contrutor da classe
     *
     * @param endServidor
     * @throws Exception
     */
    public Cliente(String endServidor) throws Exception {
        // Instancia as referencias de entrada e saida do socket
        socket = new Socket(endServidor, 12345);
        entrada = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
        saida = new PrintWriter(socket.getOutputStream(), true);

        // Criacao da janela
        frame = new JFrame("Pedra, Papel ou Tesoura");
        mensagem = new JLabel("");
        mensagem.setBackground(Color.lightGray);
        frame.getContentPane().add(mensagem, "South");

        // Painel do jogo
        JPanel fundo = new JPanel();

        // Criacao do botoes
        b1 = new JButton("Pedra");
        b2 = new JButton("Papel");
        b3 = new JButton("Tesoura");
        createButtons();

        // Adicionando componentes aos seus parentes
        fundo.add(b1);
        fundo.add(b2);
        fundo.add(b3);
        frame.getContentPane().add(fundo, "Center");
    }

    /**
     * Metodo para criar botoes e seus metodos de resposta ao clique
     */
    private void createButtons() {
      // Botao 1
      b1.setEnabled(false);
      b1.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saida.println("JOGADA 0");
          b1.setEnabled(false);
          b2.setEnabled(false);
          b3.setEnabled(false);
        }
      });

      // Botao 2
      b2.setEnabled(false);
      b2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saida.println("JOGADA 1");
          b1.setEnabled(false);
          b2.setEnabled(false);
          b3.setEnabled(false);
        }
      });

      // Botao 3
      b3.setEnabled(false);
      b3.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saida.println("JOGADA 2");
          b1.setEnabled(false);
          b2.setEnabled(false);
          b3.setEnabled(false);
        }
      });
    }

    /**
     * Metodo de execucao do loop do jogo
     *
     * @throws Exception
     */
    public void jogar() throws Exception{
        String resposta = "";
        try{
            resposta = entrada.readLine();

            // Ao receber "BEM VINDO" comeca
            if (resposta.startsWith("BEM VINDO")){
              char qualJog = resposta.charAt(10);
              frame.setTitle("Pedra, Papel ou Tesoura - Jogador " + qualJog);

              b1.setEnabled(true);
              b2.setEnabled(true);
              b3.setEnabled(true);
            }

            // Loop principal
            while (true){
                resposta = entrada.readLine();

                // Recebe mensagem e "reage" de acordo
                if(resposta.startsWith("DERROTA")) {
                  mensagem.setText("Voce perdeu.");
                  b1.setEnabled(false);
                  b2.setEnabled(false);
                  b3.setEnabled(false);
                  break;
                }
                else if(resposta.startsWith("VITORIA")) {
                  mensagem.setText("Voce ganhou.");
                  b1.setEnabled(false);
                  b2.setEnabled(false);
                  b3.setEnabled(false);
                  break;
                }
                else if(resposta.startsWith("EMPATE")) {
                  mensagem.setText("Empate.");
                  b1.setEnabled(false);
                  b2.setEnabled(false);
                  b3.setEnabled(false);
                  break;
                }
                else if(resposta.startsWith("MENSAGEM")) {
                  mensagem.setText(resposta.substring(9));
                }
                else {
                  mensagem.setText("NADA");
                }
            }

            // Encerra conexao
            saida.println("FECHAR");
        }finally{
            socket.close();
        }
    }

    /**
     * Metodo para perguntar se quer jogar novamente
     */
    private boolean jogarDenovo(){
        int resposta = JOptionPane.showConfirmDialog(frame,
          "Deseja jogar novamente?", "Pedra, Papel ou Tesoura",
          JOptionPane.YES_NO_OPTION);

        frame.dispose();
        return resposta == JOptionPane.YES_OPTION;
    }

    /**
     * Metodo principal
     *
     * @param args argumentos do programa
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
      while (true){
        Cliente cliente = new Cliente("127.0.0.1");
        cliente.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cliente.frame.setSize(300,100);
        cliente.frame.setVisible(true);
        cliente.frame.setResizable(false);
        cliente.jogar();

        if (!cliente.jogarDenovo()){
            break;
        }
      }
    }
}
