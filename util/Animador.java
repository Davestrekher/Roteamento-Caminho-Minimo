/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 01/04/2026
* Ultima alteracao.: 10/04/2026
* Nome.............: Animador
* Funcao...........: Classe que altera diretamente a interface e realiza
                     animacoes durante a execucao do algoritmo de dijkstra
*************************************************************** */

package util;

import modelo.SimulacaoContexto;

import javafx.application.Platform;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;;
import javafx.scene.text.Text;

public class Animador{
  private SimulacaoContexto ctx = new SimulacaoContexto();

  private int idSimulacao;

  private final int TEMPO_GERAL = 800;
  private final int TEMPO_PROVISORIO = 300;

  /* ***************************************************************
	* Metodo: Animador
	* Funcao: Construtor da classe Animador
	* Parametros: idSimulacao = id da simulacao atual
	* Retorno: Objeto da classe Animador
	*************************************************************** */
  public Animador(int idSimulacao){
    this.idSimulacao = idSimulacao;
  }//Fim do metodo Animador

  /* ***************************************************************
	* Metodo: verticeProvisorio
	* Funcao: Realiza um rapido destaque de um vertice
	* Parametros: imagem = imagem que representa o vertice na tela
	* Retorno: void
	*************************************************************** */
  public void verticeProvisorio(ImageView imagem) {
    try {
      final String styleAnteriorLocal = imagem.getStyle();

      //Destaca
      Platform.runLater(() -> {
        imagem.setOpacity(1.0);

        imagem.setScaleX(1.2);
        imagem.setScaleY(1.2);
      });

      esperar(TEMPO_PROVISORIO);//Controla o tempo da animacao

      //Volta ao normal
      Platform.runLater(() -> {
        imagem.setOpacity(0.7);

        imagem.setStyle(styleAnteriorLocal);

        imagem.setScaleX(1.0);
        imagem.setScaleY(1.0);
      });

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }//Fim do metodo verticeProvisorio

  /* ***************************************************************
	* Metodo: arestaProvisoria
	* Funcao: Realiza um rapido destaque de uma aresta
	* Parametros: conexao = linha que representa a aresta na tela
	* Retorno: void
	*************************************************************** */
  public void arestaProvisoria(Line conexao) {
    try {
      //Destaca
      Platform.runLater(() -> {
        conexao.setOpacity(1.0);
        conexao.setStroke(javafx.scene.paint.Color.YELLOW);
        conexao.setStrokeWidth(11);
      });

      esperar(TEMPO_GERAL);//Controla o tempo da animacao

      //Volta ao normal
      Platform.runLater(() -> {
        conexao.setOpacity(0.7);
        //conexao.setStroke(javafx.scene.paint.Color.RED);
        conexao.setStrokeWidth(9);
      });

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }//Fim do metodo arestaProvisoria

  /* ***************************************************************
	* Metodo: verticePermanente
	* Funcao: Realiza um longo destaque de um vertice
	* Parametros: imagem = imagem que representa o vertice na tela
	* Retorno: void
	*************************************************************** */
  public void verticePermanente(ImageView imagem) {
    try {
      final String styleAnteriorLocal = imagem.getStyle();
      Platform.runLater(() -> {
        //Destaca o vertice
        imagem.setOpacity(1.0);
        imagem.setImage(new Image("/assets/tanqueVerde.png"));

        //Pequeno “pop” visual
        imagem.setScaleX(1.2);
        imagem.setScaleY(1.2);
      });

      esperar(TEMPO_GERAL);

      Platform.runLater(() -> {
        //Volta o tamanho, mas mantem o destaque
        imagem.setOpacity(0.7);
        imagem.setStyle(styleAnteriorLocal);
        imagem.setScaleX(1.0);
        imagem.setScaleY(1.0);
      });

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }//Fim do metodo verticePermanente

  /* ***************************************************************
	* Metodo: arestaPermanente
	* Funcao: Realiza um destaque imediato de uma aresta
	* Parametros: conexao = linha que representa a aresta na tela
	* Retorno: void
	*************************************************************** */
  public void arestaPermanente(Line conexao) {
    Platform.runLater(() -> {
      //Destaca a aresta
      conexao.setOpacity(1.0);
      conexao.setStroke(Color.LIMEGREEN);
      conexao.setStrokeWidth(10);
      conexao.setEffect(new DropShadow(15, Color.LIMEGREEN));
    });
  }//Fim do metodo arestaPermanente

  /* ***************************************************************
	* Metodo: destaqueTexto
	* Funcao: Realiza um longo destaque de um texto
	* Parametros: texto = texto que representa o estado de um vertice na tela
	* Retorno: void
	*************************************************************** */
  public void destaqueTexto(Text texto) {
    try {

      Platform.runLater(() -> {
        //Pequeno pop visual
        texto.setScaleX(1.2);
        texto.setScaleY(1.2);
      });

      esperar(TEMPO_GERAL);

      Platform.runLater(() -> {
        //Volta ao normal (mantendo consistência visual)
        texto.setScaleX(1.0);
        texto.setScaleY(1.0);
      });

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }//Fim do metodo destaqueTexto

  /* ***************************************************************
	* Metodo: destaca
	* Funcao: Destaca imagens na tela aumentando sua opacidade
	* Parametros: imagem = imagem que representa o vertice na tela
	* Retorno: void
	*************************************************************** */
  public void destaca(ImageView imagem){
    Platform.runLater(() -> {
      imagem.setOpacity(1.0);
    });
  }//Fim do metodo destaca

  /* ***************************************************************
	* Metodo: alterarEstado
	* Funcao: Altera o texto que representa o estado de um vertice na tela
	* Parametros: texto = texto que representa o estado de um vertice na tela
                distancia = distancia do vertice, predecessor = id do vertice
                predecessor
	* Retorno: void
	*************************************************************** */
  public void alterarEstado(Text texto, String distancia, String predecessor){
    Platform.runLater(() -> {
      texto.setText("(" + distancia + ", " + predecessor + ")");
    });
  }//Fim do metodo alterarEstado

  /* ***************************************************************
	* Metodo: esperar
	* Funcao: Esperar por um certo tempo enquanto checa se a simulacao nao se encerrou.
	          Caso a simulacao tenha se encerrado, retorna imediatamente
	* Parametros: tempo = tempo de espera em ms
	* Retorno: void
	*************************************************************** */
  public void esperar(int tempo) throws InterruptedException{
    for (int i = 0; i < (tempo/10); i++){
      if (this.idSimulacao != ctx.getIdSimulacao()){
        return;
      }
      Thread.sleep(10);
    }
  }//Fim do metodo esperar
}//Fim da classe Animador
