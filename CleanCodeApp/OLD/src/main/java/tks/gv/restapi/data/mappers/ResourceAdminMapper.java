//package tks.gv.restapi.data.mappers;
//
//import tks.gv.model.logic.users.ResourceAdmin;
//import tks.gv.restapi.data.dto.ResourceAdminDTO;
//
//import java.util.UUID;
//
//public class ResourceAdminMapper {
//
//    public static ResourceAdminDTO toJsonUser(ResourceAdmin resourceAdmin) {
//        if (resourceAdmin == null) {
//            return null;
//        }
//
//        return new ResourceAdminDTO(resourceAdmin.getId().toString(),
//                resourceAdmin.getLogin(),
//                resourceAdmin.getPassword(),
//                resourceAdmin.isArchive());
//    }
//
//    public static ResourceAdmin fromJsonUser(ResourceAdminDTO resourceAdminDTO) {
//        if (resourceAdminDTO == null) {
//            return null;
//        }
//
//        ResourceAdmin newAdmin = new ResourceAdmin(resourceAdminDTO.getId() != null ? UUID.fromString(resourceAdminDTO.getId()) : null,
//                resourceAdminDTO.getLogin(), resourceAdminDTO.getPassword());
//        newAdmin.setArchive(resourceAdminDTO.isArchive());
//        return newAdmin;
//    }
//}
