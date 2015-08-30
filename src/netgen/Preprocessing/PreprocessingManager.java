package netgen.Preprocessing;

public class PreprocessingManager
{
	/* Will contain logic for running preprocessing components on the Corpus
	* object. Will provide an entry point to the network generator level.
	* 
	* Should provide convenience functions to hide the complexity of the level
	* or alternatively allow for the passing of a parameters list for more 
	* fine control over how preprocessing is conducted.
	* 
	* It is important to keep the seperation of responsibilties. Prerocessing 
	* component logic should not be in here, only the scheduling and execution 
	* of them. 
	* 
	* The concept of scheduling might be best handled with a requirements 
	* system. That is, a component can have a stated expectation of how the 
	* input corpus has been processed already and throw an exception if you 
	* are trying to perform actions in a wrong order.
	*/
	
	
}