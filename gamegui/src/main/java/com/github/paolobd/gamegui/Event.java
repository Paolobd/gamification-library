package com.github.paolobd.gamegui;

public class Event {
    private String id;
    private String url;
    private EventType eventType;

    public Event(String id, String url, EventType eventType) {
        this.id = id;
        this.url = url;
        this.eventType = eventType;
    }

    @SuppressWarnings("unused")
    public Event(){
    }

    @Override
    public String toString(){
        return "Id: " + this.id + " at site: " + url + " with type: " + this.eventType + "\n";
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public String getUrl() {
        return url;
    }

    @SuppressWarnings("unused")
    public EventType getEventType() {
        return eventType;
    }
}
