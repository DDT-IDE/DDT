package melnorme.lang.ide.core.utils;

public interface ICommonEventListener<SOURCE, EVENT_OBJ> {
	
	public void notifyUpdateEvent(SOURCE source, EVENT_OBJ eventObject);
	
}