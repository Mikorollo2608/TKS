package tks.gv.reservations;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import tks.gv.courts.Court;
import tks.gv.users.Client;
import tks.gv.exceptions.ReservationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Reservation {
    private final UUID id;

    @NotNull
    private final Client client;
    @NotNull
    private final Court court;
    private final LocalDateTime beginTime;

    private LocalDateTime endTime = null;
    private double reservationCost;

    public Reservation(UUID id, Client client, Court court, LocalDateTime beginTime) {
        this.id = id;
        this.client = client;
        this.court = court;
        this.beginTime = (beginTime == null) ? LocalDateTime.now() : beginTime;
    }

    public Reservation(UUID id, Client client, Court court, LocalDateTime beginTime, LocalDateTime endTime, double reservationCost) {
        this.id = id;
        this.client = client;
        this.court = court;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.reservationCost = reservationCost;
    }

    public int getReservationHours() {
        int hours = 0;

        if (endTime != null) {
            long duration = Duration.between(beginTime, endTime).getSeconds();
            int hoursDur = (int) (duration / 3600);
            int minutesDur = (int) ((duration / 60) % 60);

            if (!(hoursDur == 0 && minutesDur == 0)) {
                hours = (minutesDur == 0) ? hoursDur : (hoursDur + 1);
            }
        }

        return hours;
    }

    public void endReservation(LocalDateTime endingDate) {
        if (endTime == null) {
            endTime = (endingDate == null) ? LocalDateTime.now() : endingDate;
            if (Duration.between(beginTime, endTime).isNegative()) {
                endTime = beginTime;
            }

            court.setRented(false);

            if (getReservationHours() <= client.clientMaxHours()) {
                reservationCost = getReservationHours() * court.getBaseCost();
            } else {
                reservationCost = court.getBaseCost() *
                        (client.clientMaxHours() + (getReservationHours() - client.clientMaxHours()) * 1.5);
            }
            reservationCost *= (1.0 - client.applyDiscount());
        } else {
            throw new ReservationException("Ta rezerwacja juz sie zakonczyla i nie mozna zmienic jej daty!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }
}
