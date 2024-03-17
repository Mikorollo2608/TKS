package tks.gv.data.mappers.entities;

import tks.gv.courts.Court;
import tks.gv.data.entities.ClientEntity;
import tks.gv.data.entities.CourtEntity;
import tks.gv.data.entities.ReservationEntity;
import tks.gv.reservations.Reservation;
import tks.gv.users.Client;

import java.util.UUID;

public class ReservationMapper {
    public static ReservationEntity toMongoReservation(Reservation reservation) {
        return new ReservationEntity(reservation.getId().toString(), reservation.getClient().getId().toString(),
                reservation.getCourt().getId().toString(), reservation.getBeginTime(), reservation.getEndTime(),
                reservation.getReservationCost());
    }

    public static Reservation fromMongoReservation(ReservationEntity reservationMapper) {
        Reservation reservation = new Reservation(UUID.fromString(reservationMapper.getId()),
                new Client(UUID.fromString(reservationMapper.getClientId()),"","","","",""),
                new Court(UUID.fromString(reservationMapper.getCourtId()),0,0,0),
                reservationMapper.getBeginTime());
        if (reservationMapper.getEndTime() != null) {
            reservation.endReservation(reservationMapper.getEndTime());
        }
        return reservation;
    }
}
