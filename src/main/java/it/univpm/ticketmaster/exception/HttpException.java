package it.univpm.ticketmaster.exception;

public class HttpException extends Exception{
    private int code;
	public HttpException(int code) {
        super("Request returned with " +code+" status code");
        this.code=code;
	}
    public HttpException(String msg) {
        super(msg);
	}
    

    public int getCode() {
        return this.code;
    }
}