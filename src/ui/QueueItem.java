package ui;

import actors.Action;

public class QueueItem {
	private Action action;
	
	public QueueItem() {}
	
	public Action getAction() { return action; }
	public boolean setAction(Action a) { if (a != null) action = a; return (a != null); }

}
