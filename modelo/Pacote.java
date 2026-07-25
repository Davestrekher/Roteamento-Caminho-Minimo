/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 19/03/2026
* Ultima alteracao.: 10/04/2026
* Nome.............: Pacote
* Funcao...........: Classe que serve para representar um pacote,
                     com a imagem que o representa na tela e seu
                     atributo de TTL, que indica quantos hops o
                     pacote ainda pode fazer antes de ser destruido.
*************************************************************** */

package modelo;

import java.util.ArrayList;
import java.util.Stack;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pacote{
  private ImageView imagem;
  private int origem;//Roteador de origem
  private int destino;//Roteador destino

  private int idSimulacao;

  private Image[] imagensDaAnimacao;
  private Stack<Roteador> pilhaDeRoteadores;

  SimulacaoContexto ctx = new SimulacaoContexto();

  /* ***************************************************************
	* Metodo: Pacote
	* Funcao: Construtor da classe
	* Parametros: origem = ip do roteador de origem, destino = ip do
                roteador de destino, imagem = a imagem que representa
                o pacote na tela, ttl = time to live, o numero de hops
                que o pacote ainda pode passar antes de ser destruido.
	* Retorno: Objeto da classe Pacote
	*************************************************************** */
  public Pacote(int origem, int destino, ImageView imagem, int idSimulacao){
    this.origem = origem;
    this.destino = destino;
    this.imagem = imagem;
    this.idSimulacao = idSimulacao;

    this.imagensDaAnimacao = new Image[]{
			new Image(getClass().getResourceAsStream("/assets/pomba00.png")),
			new Image(getClass().getResourceAsStream("/assets/pomba01.png")),
			new Image(getClass().getResourceAsStream("/assets/pomba02.png")),
			new Image(getClass().getResourceAsStream("/assets/pomba03.png")),
			new Image(getClass().getResourceAsStream("/assets/pomba04.png")),
			new Image(getClass().getResourceAsStream("/assets/pomba05.png")),
			new Image(getClass().getResourceAsStream("/assets/pomba06.png"))
		};
  }//Fim do metodo Pacote

  /* ***************************************************************
	* Metodo: movimento
	* Funcao: Move a imagem do pacote de um roteador a outro.
	* Parametros: x2 = coordenada x do roteador destino, y2 = coordenada
                y do roteador destino.
	* Retorno: void
	*************************************************************** */
  public void movimento(double x2, double y2){
    //x1 e y1 sao as coordenadas atuais da imagem
    double x1 = this.imagem.getLayoutX();
    double y1 = this.imagem.getLayoutY();

    //dx e dy sao a diferenca entre as coordenadas de origem e de destino
    double dx = x2 - x1;
    double dy = y2 - y1;

    //Distancia calculada pelo triangulo de pitagoras
    double distancia = Math.sqrt(dx*dx + dy*dy);
    double velocidade = 100.0;

    //Tempo da animacao obtido pela relacao entre distancia e velocidade
    double tempo = distancia / velocidade;

    //Quantos quadros serao animados por segundo
    int fps = 60;
    //Quantos frames no total terao que ser gerados (relacao entre tempo e quadros por segundo)
    int frames = (int) (tempo * fps);

    //Se dx for menor que zero (movimento para a esquerda), espelha a imagem
    double direcao = (dx < 0) ? -1 : 1;

    Platform.runLater(() -> {
        this.imagem.setScaleX(direcao);
    });

    //Para cada quadro
    for(int i = 0; i < frames; i++){
      //Se a simulacao, for reiniciada, as threads tem que ser destruidas
      if (this.idSimulacao != ctx.getIdSimulacao()){
        return;
      }

      //parametro sao quantos pixels serao movidos nessa iteracao. Naturalmente, o valor aumenta a cada iteracao, ate chegar a 1.
      double parametro = (double) i / frames;

      //Coordenadas calculadas pela equacao parametrica da reta
      double x = x1 + parametro * dx;
      double y = y1 + parametro * dy;

      //Troca a imagem a cada 6 frames (~10 fps se fps=60)
      int trocaSpriteACada = 6;
      int frameIndex = (i / trocaSpriteACada) % imagensDaAnimacao.length;

      //Platform.runlater para mover a imagem na thread principal do JavaFX, evitando problemas na interfaces
      Platform.runLater(() -> {
        double offsetX = this.imagem.getFitWidth() / 2;
        double offsetY = this.imagem.getFitHeight() / 2;

        this.imagem.setLayoutX(x - offsetX);
        this.imagem.setLayoutY(y - offsetY);

        this.imagem.setImage(imagensDaAnimacao[frameIndex]);
      });

       try {
         Thread.sleep(1000 / fps);
       }catch (InterruptedException e) {
         e.printStackTrace();
       }
    }
  }//Fim do metodo movimento

  /* ***************************************************************
	* Metodo: getDestino
	* Funcao: Retorna o identificador do roteador de destino final deste
            pacote.
	* Parametros: Nenhum
	* Retorno: int
	*************************************************************** */
  public int getDestino(){
    return this.destino;
  }//Fim do metodo getDestino

  /* ***************************************************************
	* Metodo: getOrigem
	* Funcao: Retorna o identificador do primeiro roteador a enviar este
            pacote.
	* Parametros: Nenhum
	* Retorno: int
	*************************************************************** */
  public int getOrigem(){
    return this.origem;
  }//Fim do metodo getOrigem

  /* ***************************************************************
	* Metodo: getImagem
	* Funcao: Retorna a imagem que representa este pacote
	* Parametros: Nenhum
	* Retorno: ImageView
	*************************************************************** */
  public ImageView getImagem(){
    return this.imagem;
  }//Fim do metodo getImagem

  /* ***************************************************************
	* Metodo: getCaminho
	* Funcao: Retorna uma pilha com a ordem dos roteadores que formam
	          o caminho que o pacote deve tomar para chegar ao destino
	* Parametros: Nenhum
	* Retorno: Stack<Roteador>
	*************************************************************** */
  public Stack<Roteador> getCaminho(){
    return this.pilhaDeRoteadores;
  }//Fim do metodo getCaminho

  /* ***************************************************************
	* Metodo: setCaminho
	* Funcao: Define a pilha com a ordem dos roteadores que formam
	          o caminho que o pacote deve tomar para chegar ao destino
	* Parametros: pilhaDeRoteadores = caminho de roteadores
	* Retorno: void
	*************************************************************** */
  public void setCaminho(Stack<Roteador> pilhaDeRoteadores){
    this.pilhaDeRoteadores = pilhaDeRoteadores;
  }//Fim do metodo setCaminho

  /* ***************************************************************
	* Metodo: proximoRoteador
	* Funcao: Retira o proximo roteador do caminho
	* Parametros: Nenhum
	* Retorno: Objeto da classe roteador
	*************************************************************** */
  public Roteador proximoRoteador(){
    return pilhaDeRoteadores.pop();
  }//Fim do metodo proximoRoteador

  /* ***************************************************************
	* Metodo: getIdSimulacao
	* Funcao: Retorna o id da simulacao atual
	* Parametros: Nenhum
	* Retorno: int
	*************************************************************** */
  public int getIdSimulacao(){
    return this.idSimulacao;
  }//Fim do metodo getIdSimulacao

  /* ***************************************************************
	* Metodo: setImagem
	* Funcao: Define a imagem que representa este pacote
	* Parametros: imagem = imagem que passa a representar este pacote
	* Retorno: void
	*************************************************************** */
  public void setImagem(ImageView imagem){
    this.imagem = imagem;
  }//Fim do metodo setImagem
}//Fim da classe Pacote
