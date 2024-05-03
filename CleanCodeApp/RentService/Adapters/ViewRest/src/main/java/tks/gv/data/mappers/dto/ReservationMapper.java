package tks.gv.data.mappers.dto;

import tks.gv.Reservation;
import tks.gv.restapi.data.dto.ReservationDTO;

import java.util.UUID;

public class ReservationMapper {

    public static ReservationDTO toJsonReservation(Reservation reservation) {
        return new ReservationDTO(reservation.getId().toString(),
                ClientMapper.toDTO(reservation.getClient()),
                CourtMapper.toJsonCourt(reservation.getCourt()),
                reservation.getBeginTime(),
                reservation.getEndTime(),
                reservation.getReservationCost()
        );
    }

    public static Reservation fromJsonReservation(ReservationDTO reservationDTO) {
        Reservation newReservation = new Reservation(reservationDTO.getId() != null ? UUID.fromString(reservationDTO.getId()) : null,
                ClientMapper.fromDTO(reservationDTO.getClient()),
                CourtMapper.fromJsonCourt(reservationDTO.getCourt()),
                reservationDTO.getBeginTime()
        );
        if (reservationDTO.getEndTime() != null) {
            newReservation.endReservation(reservationDTO.getEndTime());
        }
        return newReservation;
    }
}
