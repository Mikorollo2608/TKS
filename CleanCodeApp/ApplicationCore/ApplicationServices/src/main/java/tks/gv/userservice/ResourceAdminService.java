//package tks.gv.userservice;
//
//import com.mongodb.client.model.Filters;
//
//import jakarta.validation.UnexpectedTypeException;
//
//import lombok.NoArgsConstructor;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import tks.gv.data.repositories.UserMongoRepository;
//import tks.gv.model.exceptions.MyMongoException;
//import tks.gv.model.exceptions.UserException;
//import tks.gv.model.exceptions.UserLoginException;
//import tks.gv.model.logic.users.Admin;
//import tks.gv.model.logic.users.Client;
//import tks.gv.model.logic.users.ResourceAdmin;
//import tks.gv.model.logic.users.User;
//
//import tks.gv.restapi.data.dto.ResourceAdminDTO;
//import tks.gv.restapi.data.mappers.ResourceAdminMapper;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@NoArgsConstructor
//public class ResourceAdminService implements UserService {
//
//    private UserMongoRepository userRepository;
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    public ResourceAdminService(UserMongoRepository userRepository) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = new BCryptPasswordEncoder();
//    }
//
//    public ResourceAdminDTO registerResourceAdmin(String login, String password) {
//        try {
//            return ResourceAdminMapper
//                    .toJsonUser((ResourceAdmin) userRepository.create(
//                            new ResourceAdmin(null, login, passwordEncoder.encode(password)))
//                    );
//        } catch (MyMongoException | UnexpectedTypeException exception) {
//            throw new UserException("Nie udalo sie zarejestrowac administratora danych w bazie! - " + exception.getMessage());
//        }
//    }
//
//    public ResourceAdminDTO getResourceAdminById(String resourceAdminId) {
//        User resourceAdmin = userRepository.readByUUID(UUID.fromString(resourceAdminId), ResourceAdmin.class);
//        return resourceAdmin != null ? ResourceAdminMapper.toJsonUser((ResourceAdmin) resourceAdmin) : null;
//    }
//
//    public List<ResourceAdminDTO> getAllResourceAdmins() {
//        List<ResourceAdminDTO> list = new ArrayList<>();
//        for (var user : userRepository.readAll(ResourceAdmin.class)) {
//            if (user instanceof ResourceAdmin resAdmin) {
//                list.add(ResourceAdminMapper.toJsonUser(resAdmin));
//            }
//        }
//        return list;
//    }
//
//    public ResourceAdminDTO getResourceAdminByLogin(String login) {
//        var list = userRepository.read(Filters.eq("login", login), ResourceAdmin.class);
//        if (list.isEmpty() || (list.get(0) instanceof Admin || list.get(0) instanceof Client)) {
//            return null;
//        }
//        return ResourceAdminMapper.toJsonUser((ResourceAdmin) list.get(0));
//    }
//
//    public List<ResourceAdminDTO> getResourceAdminByLoginMatching(String login) {
//        List<ResourceAdminDTO> list = new ArrayList<>();
//        for (var user : userRepository.read(Filters.and(Filters.regex("login", ".*%s.*".formatted(login)),
//                Filters.eq("_clazz", "resourceadmin")), ResourceAdmin.class)) {
//            list.add(ResourceAdminMapper.toJsonUser((ResourceAdmin) user));
//        }
//        return list;
//    }
//
//    public void modifyResourceAdmin(ResourceAdminDTO modifiedResourceAdmin) {
//        var list = userRepository.read(Filters.and(
//                Filters.eq("login", modifiedResourceAdmin.getLogin()),
//                Filters.ne("_id", modifiedResourceAdmin.getId())), ResourceAdmin.class);
//        if (!list.isEmpty()) {
//            throw new UserLoginException("Nie udalo sie zmodyfikowac podanego administratora - " +
//                    "proba zmiany loginu na login wystepujacy juz u innego administratora");
//        }
//
//        if (!userRepository.updateByReplace(UUID.fromString(modifiedResourceAdmin.getId()),
//                ResourceAdminMapper.fromJsonUser(modifiedResourceAdmin))) {
//            throw new UserException("Nie udalo sie zmodyfikowac podanego administratora.");
//        }
//    }
//
//    public void activateResourceAdmin(String resourceAdminId) {
//        userRepository.update(UUID.fromString(resourceAdminId), "archive", false);
//    }
//
//    public void deactivateResourceAdmin(String resourceAdminId) {
//        userRepository.update(UUID.fromString(resourceAdminId), "archive", true);
//    }
//
////    public void changeResourceAdminPassword(String id, ChangePasswordDTORequest changePasswordDTO) {
////        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
////
////        if (!passwordEncoder.matches(changePasswordDTO.getActualPassword(), user.getPassword())) {
////            throw new IllegalStateException("Niepoprawne aktualne haslo!");
////        }
////        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmationPassword())) {
////            throw new IllegalStateException("Podane hasla roznia sie!");
////        }
////
////        userRepository.update(UUID.fromString(id), "password",
////                passwordEncoder.encode(changePasswordDTO.getNewPassword()));
////    }
//
//    @Override
//    public int usersSize() {
//        return userRepository.readAll(User.class).size();
//    }
//
//
//    /*----------------------------------------------HANDLE UUID----------------------------------------------*/
//
//    public ResourceAdminDTO getResourceAdminById(UUID resourceAdminId) {
//        return getResourceAdminById(resourceAdminId.toString());
//    }
//
//    public void activateResourceAdmin(UUID resourceAdminId) {
//        activateResourceAdmin(resourceAdminId.toString());
//    }
//
//    public void deactivateResourceAdmin(UUID resourceAdminId) {
//        deactivateResourceAdmin(resourceAdminId.toString());
//    }
//
////    public void changeResourceAdminPassword(UUID id, ChangePasswordDTORequest changePasswordDTO) {
////        changeResourceAdminPassword(id.toString(), changePasswordDTO);
////    }
//}
