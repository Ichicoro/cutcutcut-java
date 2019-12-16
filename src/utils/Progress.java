package utils;

public class Progress {
	int value = 0;
	int total = 0;
	
	public Progress(int value, int total) {
		setValue(value);
		setTotal(total);
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}

}
