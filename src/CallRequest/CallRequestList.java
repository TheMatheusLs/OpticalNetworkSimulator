package src.CallRequest;

import java.util.ArrayList;
import java.util.List;
/**
 * Descreve o gerenciamento da lista de requisi��es de conex�o da rede.
 * @author Andr� 
 */
public class CallRequestList {
	/**
	 * N�mero de requisi��es estabelecidas.
	 * @author Andr� 			
	 */	
	private transient int numbOfEstCallReq;
	/**
	 * Lista das conex�es ativas da rede.
	 * @author Andr� 			
	 */
	private transient List<CallRequest> callReqList;
	/**
	 * Construtor da classe.
	 * @author Andr�		
	 */
	public CallRequestList() {		
		this.callReqList = new ArrayList<CallRequest>();
		this.numbOfEstCallReq = 0;
	}
	/**
	 * M�todo para retornar o n�mero de conex�es estabelecidas.
	 * @return O atributo numbOfEstCallReq
	 * @author Andr� 			
	 */		 
	 public int getNumberOfEstablishCalls(){
		 return this.numbOfEstCallReq;
	 }
	 /**
	  * M�todo para adicionar um nova conex�o a lista.
	  * @param callRequest
	  * @author Andr� 			
	  */	 
	 public void addCall(final CallRequest callRequest){
		 this.callReqList.add(callRequest);
		 this.numbOfEstCallReq++;		 
	 }
	 /**
	  * M�todo para remover as conex�es que j� expiraram da rede.
	  * @param acumulatedTime
	  * @author Andr� 			
	  */	 
	public void removeCallRequest(final double acumulatedTime){
		for(int x=0;x<this.callReqList.size();x++){
			final CallRequest callRequest = this.callReqList.get(x);
			final double time = getDecayTimeInCall(callRequest);		 
			if(time<acumulatedTime){

				// Diminuir os slots que estão sendo usados
				//callRequest.getRoute().decreasesSlotsOcupy(callRequest.getFrequencySlots());

				this.callReqList.get(x).desallocate();
				this.callReqList.remove(x);
				x--;
			}			 
		}  	  
	}
	 /**
	  * M�todo para retornar o tempo de queda.
	  * @param callRequest
	  * @return O atributo decayTime
	  * @author Andr� 			
	  */	 
	 private double getDecayTimeInCall(final CallRequest callRequest){
		 return callRequest.getDecayTime();		 
	 }
	 /**
	  * M�todo para resetar o tempo das conex�es ativas.
	  * @param acumulatedTime
	  * @author Andr� 			
	  */	 
	 public void resetTimeCall(final double acumulatedTime){		 
		 for(int i=0;i<this.callReqList.size();i++){
			 final CallRequest callRequest = this.callReqList.get(i);
			 final double time = getDecayTimeInCall(callRequest);
			 this.callReqList.get(i).setDecayTime(time-acumulatedTime);
		 }
	 }
	 /**
	  * M�todo para resetar a lista de conex�es.
	  * @author Andr� 			
	  */	 
	 public void eraseCallList(){
		 this.callReqList = new ArrayList<CallRequest>();
		 this.numbOfEstCallReq = 0;	 
	 }

	
	public void desallocateAllRequests(){
		removeCallRequest(Double.MAX_VALUE);
	}
}
