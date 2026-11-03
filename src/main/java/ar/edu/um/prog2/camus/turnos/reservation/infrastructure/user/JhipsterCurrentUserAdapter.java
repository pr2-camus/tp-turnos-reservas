package ar.edu.um.prog2.camus.turnos.reservation.infrastructure.user;

import ar.edu.um.prog2.camus.turnos.domain.User;
import ar.edu.um.prog2.camus.turnos.error.BusinessException;
import ar.edu.um.prog2.camus.turnos.error.ErrorKind;
import ar.edu.um.prog2.camus.turnos.repository.UserRepository;
import ar.edu.um.prog2.camus.turnos.reservation.domain.model.ReservationUser;
import ar.edu.um.prog2.camus.turnos.reservation.domain.ports.out.CurrentUserProvider;
import ar.edu.um.prog2.camus.turnos.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class JhipsterCurrentUserAdapter implements CurrentUserProvider {

    private final UserRepository userRepository;

    public JhipsterCurrentUserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ReservationUser getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(this::notFound);
        User user = userRepository.findOneByLogin(login).orElseThrow(this::notFound);
        return new ReservationUser(user.getId(), user.getFirstName(), user.getLastName());
    }

    private BusinessException notFound() {
        return new BusinessException("USER_NOT_FOUND", ErrorKind.NOT_FOUND, "No hay un usuario autenticado.");
    }
}
