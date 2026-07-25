/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 18/03/2026
* Ultima alteracao.: 10/04/2026
* Nome.............: Aresta
* Funcao...........: Classe que serve para representar uma aresta
                     em um grafo, contendo o vertice de destino e
                     o peso da aresta.
*************************************************************** */

package modelo;

import javafx.scene.shape.Line;

public class Aresta<Roteador>{

  private Roteador destino;
  private int peso;
  private Line linha;

  /* ***************************************************************
	* Metodo: Aresta
	* Funcao: Construtor da classe
	* Parametros: destino = vertice de destino da aresta, peso = peso
                da aresta
	* Retorno: Objeto da classe Aresta
	*************************************************************** */
  public Aresta(Roteador destino, int peso, Line linha){
    this.destino = destino;
    this.peso = peso;
    this.linha = linha;
  }//Fim do metodo Aresta

  /* ***************************************************************
	* Metodo: getDestino
	* Funcao: Retorna o roteador atrelado a essa aresta (eh uma aresta direcionada)
	* Parametros: Nenhum
	* Retorno: Objeto da classe Roteador
	*************************************************************** */
  public Roteador getDestino(){
    return destino;
  }//Fim do metodo getDestino

  /* ***************************************************************
	* Metodo: getPeso
	* Funcao: Retorna o peso da aresta
	* Parametros: destino = vertice de destino da aresta, peso = peso
                da aresta
	* Retorno: int
	*************************************************************** */
  public int getPeso(){
    return this.peso;
  }//Fim do metodo getPeso

  /* ***************************************************************
	* Metodo: toString
	* Funcao: Retorna uma representacao em forma de String do objeto.
	* Parametros: Nenhum
	* Retorno: String
	*************************************************************** */
  public String toString(){
    return this.destino.toString();
  }//Fim do metodo toString

  /* ***************************************************************
	* Metodo: getLinha
	* Funcao: Retorna a linha que representa essa aresta na tela
	* Parametros: Nenhum
	* Retorno: Line
	*************************************************************** */
  public Line getLinha(){
    return this.linha;
  }//Fim do metodo getLinha
}//Fim da classe Aresta
