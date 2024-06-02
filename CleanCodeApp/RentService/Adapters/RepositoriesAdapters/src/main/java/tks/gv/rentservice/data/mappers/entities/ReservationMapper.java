package tks.gv.rentservice.data.mappers.entities;

import tks.gv.rentservice.Court;
import tks.gv.rentservice.data.entities.ReservationEntity;
import tks.gv.rentservice.Reservation;
import tks.gv.rentservice.Client;

import java.util.Objects;
import java.util.UUID;

public class ReservationMapper {
    public static ReservationEntity toReservationEntity(Reservation reservation) {
        return new ReservationEntity(Objects.requireNonNullElse(reservation.getId(), "").toString(), reservation.getClient().getId().toString(),
                reservation.getCourt().getId().toString(), reservation.getBeginTime(), reservation.getEndTime(),
                reservation.getReservationCost());
    }

    public static Reservation fromReservationEntity(ReservationEntity reservationMapper) {
        if (reservationMapper == null) return null;

        return new Reservation(UUID.fromString(reservationMapper.getId()),
                new Client(UUID.fromString(reservationMapper.getClientId()), "", ""),
                new Court(UUID.fromString(reservationMapper.getCourtId()), 0, 0, 0),
                reservationMapper.getBeginTime(), reservationMapper.getEndTime(), reservationMapper.getReservationCost());
    }
}
