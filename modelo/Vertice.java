/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 31/03/2026
* Ultima alteracao.: 10/04/2026
* Nome.............: Vertice
* Funcao...........: Classe que armazena informacoes importantes para
                     representar os vertices usados no algotimo de
                     dijkstra, assim os separando dos roteadores,
                     que se encarregam apenas de chamar o algoritmo
                     de roteamento e encaminhar o pacote, alem de
                     armazenar as informacoes graficas dos nos da
                     rede.
*************************************************************** */

package modelo;

public class Vertice{
  Roteador roteador;

  String identificador;
  String predecessor;
  int distancia;
  int estado;//0 eh provisorio, 1 eh permanente

  /* ***************************************************************
	* Metodo: getRoteador
	* Funcao: Retorna o roteador associado a esse vertice
	* Parametros: Nenhum
	* Retorno: Objeto da classe Roteador
	*************************************************************** */
  public Roteador getRoteador(){
    return roteador;
  }//Fim do metodo getRoteador

  /* ***************************************************************
	* Metodo: setRoteador
	* Funcao: Define o roteador associado a esse vertice
	* Parametros: roteador = roteador que esse vertice representa
	* Retorno: void
	*************************************************************** */
  public void setRoteador(Roteador roteador){
    this.roteador = roteador;
  }//Fim do metodo setRoteador

  /* ***************************************************************
	* Metodo: getIdentificador
	* Funcao: Retorna o identificador deste vertice
	* Parametros: Nenhum
	* Retorno: String
	*************************************************************** */
  public String getIdentificador(){
    return identificador;
  }//Fim do metodo getIdentificador

  /* ***************************************************************
	* Metodo: setIdentificador
	* Funcao: Define o identificador deste vertice
	* Parametros: identificador = rotulo do vertice
	* Retorno: void
	*************************************************************** */
  public void setIdentificador(String identificador){
    this.identificador = identificador;
  }//Fim do metodo setIdentificador

  /* ***************************************************************
	* Metodo: getPredecessor
	* Funcao: Retorna o identificador do predecessor deste vertice
	* Parametros: Nenhum
	* Retorno: String
	*************************************************************** */
  public String getPredecessor(){
    return predecessor;
  }//Fim do metodo getPredecessor

  /* ***************************************************************
	* Metodo: setIdentificador
	* Funcao: Define o identificador do predecessor deste vertice
	* Parametros: predecessor = rotulo do predecessor
	* Retorno: void
	*************************************************************** */
  public void setPredecessor(String predecessor){
    this.predecessor = predecessor;
  }//Fim do metodo setPredecessor

  /* ***************************************************************
	* Metodo: getDistancia
	* Funcao: Retorna a distancia deste vertice
	* Parametros: Nenhum
	* Retorno: int
	*************************************************************** */
  public int getDistancia(){
    return distancia;
  }//Fim do metodo getDistancia

  /* ***************************************************************
	* Metodo: setDistancia
	* Funcao: Define a distancia deste vertice
	* Parametros: distancia = soma das distancias ate este vertice
	* Retorno: void
	*************************************************************** */
  public void setDistancia(int distancia){
    this.distancia = distancia;
  }//Fim do metodo setDistancia

  /* ***************************************************************
	* Metodo: getEstado
	* Funcao: Retorna o estado deste vertice
	* Parametros: Nenhum
	* Retorno: int
	*************************************************************** */
  public int getEstado(){
    return estado;
  }//Fim do metodo getEstado

  /* ***************************************************************
	* Metodo: setEstado
	* Funcao: Define o estado deste vertice
	* Parametros: estado = 0 para provisorio, 1 para permanente
	* Retorno: void
	*************************************************************** */
  public void setEstado(int estado){
    this.estado = estado;
  }//Fim do metodo setEstado
}//Fim da classe Vertice
