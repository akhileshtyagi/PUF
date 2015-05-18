///This class represents a token within the model. Essentially this is a range of values. A touch is defined to be within a token if the pressure value of the touch falls within this range.
///This class is designed to abstract away the clustering algorithm. This makes the rest of the code far simpler to think about
///Something to look at in the future may be a clustering algorithm that is not equally distributed

public class Token{
	private double min;
	private double max;

	///TODO allow for creation of tokens with-in some number of standard deviations of a distribution
	public Token(Distribution distribution, int total_tokens, int token_index, double standard_deviations){

	}	


	///allow for creation of tokens over a distribution
	public Token(Distribution distribution, int total_tokens, int token_index){
		this(distribution.get_min(), distribution.get_max(), total_tokens, token_index);
	}


	/** Implemented in the constructor is the clustering algorithm. This determines how to split up the range into a number of tokens.

		range_min, minimum of the token range
		range_max, maximum of the token range
		total_tokens total number of tokens to split range into
		token_index, integer between 0 and total_tokens-1 indicating into which range this token falls
	**/
	public Token(double range_min, double range_max, int total_tokens, int token_index){
		//TODO check for correctness
		//clustering algorithm
		double variation = (range_max - range_min) / total_tokens;

		//variation is now the size of the tokens
		this(variation*token_index, variation*(token_index+1));
	}


	///private constructor... this is just an organization thing. I think it makes it more clear how this is working
	private Token(double min, double max){
		this.min = min;
		this.max = max;
	}


	///determines if a touch is within this token based on its pressure value
	public boolean contains(Touch touch){
		return (touch.get_pressure() < max) && (touch.get_pressure() > min);
	}
	
	
	///return minimum
	public double get_min(){
		return min;
	}


	///return the maximum
	public double get_max(){
		return max;
	}

	
	///compares This token to another to determine if they are the same
	@Override
	public boolean equals(other_token){
		return (this.min == other_token.min) && (this.max == other_token.max);
	}
}
