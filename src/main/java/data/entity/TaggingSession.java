package data.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import data.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name="tagging_sessions")
public class TaggingSession {

    @Id
    @org.hibernate.annotations.Type(type = "pg-uuid")
    @NonNull
    @Column(name="session_id")
    private UUID sessionId;

    @NotNull
    @NonNull
    @Column(name="start_date")
    @Convert(converter = data.converter.TimeConverter.class)
    //@JsonSerialize(using = ToStringSerializer.class)
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDate;

    @Column(name="end_date")
    @Convert(converter = data.converter.TimeConverter.class)
    private LocalDateTime endDate;

    @NonNull
    @JoinColumn(name="user_id")
    @ManyToOne
    private User user;
}
