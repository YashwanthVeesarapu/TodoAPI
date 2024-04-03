package us.redsols.todo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import us.redsols.todo.model.Notification;
import us.redsols.todo.repo.NotificationsRepository;

@Service
public class NotificationService {
    private final NotificationsRepository notificationsRepository;

    public NotificationService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    // save Notifications

    public String saveNotifications(List<Notification> notifications) {
        notificationsRepository.saveAll(notifications);
        return "success";
    }

    // remove all Nofiications

    public String removeAllNotifications() {
        notificationsRepository.deleteAll();

        return "success";
    }

    // get all Notifications

    public List<Notification> getAllNotifications() {
        return notificationsRepository.findAll();
    }

    // remove single Notification

    public String removeNotification(String id) {
        notificationsRepository.deleteById(id);
        return "success";
    }

    // get single Notification by todoId

    public Notification getNotificationByTodoId(String todoId) {
        return notificationsRepository.findByTodoId(todoId);
    }

    // add single Notification

    public String addNotification(Notification notification) {
        notificationsRepository.insert(notification);
        return "success";
    }

    // edit single Notification

    public String editNotification(Notification notification) {
        notificationsRepository.save(notification);
        return "success";
    }

}
