/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 16/03/2026
* Ultima alteracao.: 10/04/2026
* Nome.............: Roteador
* Funcao...........: Classe que armazena as informaoces importantes
                     representar os roteadores da sub-rede representados
                     na tela.
*************************************************************** */

package modelo;

import java.util.concurrent.Semaphore;
import java.util.EmptyStackException;
import java.util.LinkedList;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Roteador{
  //Representam e identificam cada roteador individual na tela
  private ImageView imagem;
  private int identificador;
  private String idVisual;
  private LinkedList<Aresta<Roteador>> vizinhos;
  private Pane raiz;
  private Dijkstra dijkstra;
  private Text rotuloEstado;

  private int idSimulacao;

  /* ***************************************************************
	* Metodo: Roteador
	* Funcao: Construtor da classe
	* Parametros: imagem = representacao do roteador na tela, identificador =
                numero que identifica cada um dos roteadores.
	* Retorno: Objeto da classe Roteador
	*************************************************************** */
  public Roteador(ImageView imagem, int identificador, Pane raiz, Dijkstra dijkstra, Text rotuloEstado, String idVisual){
    this.imagem = imagem;
    this.identificador = identificador;
    this.raiz = raiz;
    this.dijkstra = dijkstra;
    this.rotuloEstado = rotuloEstado;
    this.idVisual = idVisual;
  }//Fim do metodo Roteador

  /* ***************************************************************
	* Metodo: getIdentificador
	* Funcao: Retorna o identificador deste roteador.
	* Parametros: Nenhum
	* Retorno: int
	*************************************************************** */
  public int getIdentificador(){
    return this.identificador;
  }//Fim do metodo getIdentificador

  /* ***************************************************************
	* Metodo: getRotuloEstado
	* Funcao: Retorna o Text que representa o estado do roteador na tela
	* Parametros: Nenhum
	* Retorno: Text
	*************************************************************** */
  public Text getRotuloEstado(){
    return this.rotuloEstado;
  }//Fim do metodo getRotuloEstado

  /* ***************************************************************
	* Metodo: setIdSimulacao
	* Funcao: Define o id da simulacao atual
	* Parametros: idSimulacao = id da simulacao atual
	* Retorno: void
	*************************************************************** */
  public void setIdSimulacao(int idSimulacao){
    this.idSimulacao = idSimulacao;
  }//Fim do metodo setIdSimulacao

  /* ***************************************************************
	* Metodo: getLayoutX
	* Funcao: Retorna a coordenada X da posicao do roteador.
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
  public double getLayoutX(){
    return imagem.getLayoutX() + (imagem.getFitWidth() / 2);
  }//Fim do metodo getLayoutX

  /* ***************************************************************
	* Metodo: getLayoutY
	* Funcao: Retorna a coordenada X da posicao do roteador.
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
  public double getLayoutY(){
    return imagem.getLayoutY() + (imagem.getFitHeight() / 2);
  }//Fim do metodo getLayoutY

  /* ***************************************************************
	* Metodo: addVizinhos
	* Funcao: Define a lista que contem todos roteadores adjacentes a
            este roteador
	* Parametros: vizinhos = lista de adjacencias
	* Retorno: void
	*************************************************************** */
  public void addVizinhos(LinkedList<Aresta<Roteador>> vizinhos){
    this.vizinhos = vizinhos;
  }//Fim do metodo addVizinhos

  /* ***************************************************************
	* Metodo: roteamento
	* Funcao: Direciona o pacote para o algoritmo de roteamento escolhido
	* Parametros: pacote = pacote com informacao de origem, destino e ttl,
                origemAtual = id do roteador de onde o pacote veio.
	* Retorno: void
	*************************************************************** */
  public void roteamento(Pacote pacote) throws InterruptedException{
    //Estabelece o caminho minimo ate o destino (esta armazenado no pacote)
    if (dijkstra.caminhoMinimo(pacote)){
      encaminhamento(pacote);
    }
  }//Fim do metodo roteamento

  /* ***************************************************************
	* Metodo: encaminhamento
	* Funcao: Simplesmente envia o pacote para todas os roteadores adjacentes
	          a este roteador
	* Parametros: pacote = pacote com informacao de origem, destino e ttl,
                origemAtual = id do roteador de onde o pacote veio.
	* Retorno: void
	*************************************************************** */
  public void encaminhamento(Pacote pacote) throws InterruptedException{
    //Para as imagens de pacotes anteriores nao ficarem paradas na tela, esse metodo eh utilizado.
    //Note que isso nao altera o travamento causado por muitos pacotes gerados, porque o pacote
    //vai ser copiado e enviado de qualquer maneira, de forma que a informacao nao eh perdida.
    Platform.runLater(() -> {
      ImageView imagemAnterior = pacote.getImagem();
      imagemAnterior.setOpacity(0);
    });

    Roteador destino;
    try{
      destino = pacote.proximoRoteador();
    }catch(EmptyStackException e){
      return;
    }

    ImageView imagem = new ImageView(new Image("/assets/pomba00.png"));
    imagem.setPreserveRatio(true);
    imagem.setSmooth(true);

    imagem.setFitWidth(25);
    imagem.setFitHeight(25);

    Pacote novoPacote = new Pacote(
        pacote.getOrigem(),
        pacote.getDestino(),
        imagem,
        pacote.getIdSimulacao()
    );

    novoPacote.setCaminho(pacote.getCaminho());

    Platform.runLater(() -> {
      imagem.setLayoutX(this.getLayoutX());
      imagem.setLayoutY(this.getLayoutY());

      raiz.getChildren().add(imagem);

      new Thread(() -> {
        //Envia o pacote
        try{
          double destinoX = destino.getLayoutX();
          double destinoY = destino.getLayoutY();

          SimulacaoContexto ctx = new SimulacaoContexto();

          novoPacote.movimento(destinoX, destinoY);

          if (ctx.getIdSimulacao() != this.idSimulacao){
            return;
          }

          //Quando o pacote chegar, encaminha-lo novamente no novo roteador
          destino.encaminhamento(novoPacote);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
      }).start();
    });


  }//Fim do metodo encaminhamento

  /* ***************************************************************
	* Metodo: getImagem
	* Funcao: Retorna a imagem que representa o roteador na tela
	* Parametros: Nenhum
	* Retorno: ImageView
	*************************************************************** */
  public ImageView getImagem(){
    return this.imagem;
  }//Fim do metodo getImagem

  /* ***************************************************************
	* Metodo: toString
	* Funcao: Retorna uma representacao em forma de String do objeto.
	* Parametros: Nenhum
	* Retorno: String
	*************************************************************** */
  @Override
  public String toString(){
    return idVisual;
  }//Fim do metodo toString
}//Fim da classe Roteador
