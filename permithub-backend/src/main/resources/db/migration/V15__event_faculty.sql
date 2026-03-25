-- V15: event_faculty
CREATE TABLE event_faculty (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eventId BIGINT NOT NULL,
    facultyId BIGINT NOT NULL, -- faculty_profile_id
    FOREIGN KEY (eventId) REFERENCES events(id),
    FOREIGN KEY (facultyId) REFERENCES faculty_profiles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
