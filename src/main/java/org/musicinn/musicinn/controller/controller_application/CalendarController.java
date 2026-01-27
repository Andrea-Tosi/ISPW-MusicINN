package org.musicinn.musicinn.controller.controller_application;

import org.musicinn.musicinn.model.Calendar;
import org.musicinn.musicinn.model.SchedulableEvent;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.bean.SchedulableEventBean;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarController {
    public List<SchedulableEventBean> getEventsForDate(LocalDate date) throws DatabaseException {
        Calendar calendar = new Calendar();
        calendar.setEvents(DAOFactory.getAnnouncementDAO().getConfirmedEventsByDate(date));

        List<SchedulableEventBean> beans = new ArrayList<>();
        for (SchedulableEvent event : calendar.getEvents()) {
            SchedulableEventBean bean = new SchedulableEventBean();
            bean.setStartingDate(event.getStartEventDay());
            bean.setStartingTime(event.getStartEventTime());
            bean.setDuration(event.getDuration());
            if (Session.getSingletonInstance().getRole().equals(Session.UserRole.ARTIST)) {
                bean.setVenueName(DAOFactory.getVenueDAO().findVenueNameByAnnouncementId(event.getId()));
            } else if (Session.getSingletonInstance().getRole().equals(Session.UserRole.MANAGER)) {
                bean.setArtistStageName(DAOFactory.getArtistDAO().findStageNameByAnnouncementId(event.getId()));
            }
            beans.add(bean);
        }

        return beans;
    }
}
