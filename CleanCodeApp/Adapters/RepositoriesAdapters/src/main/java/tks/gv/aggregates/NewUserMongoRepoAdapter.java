//package tks.gv.aggregates;
//
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//import tks.gv.data.entities.UserEntity;
//import tks.gv.repositories.UserMongoRepository;
//import tks.gv.users.User;
//
//@Component
//@Primary
//public class NewUserMongoRepoAdapter extends UserMongoRepositoryAdapter {
//    public NewUserMongoRepoAdapter(UserMongoRepository repository) {
//        super(repository);
//    }
//
//    @Override
//    protected UserEntity autoMap(User user) {
//        return super.autoMap(user);
//    }
//
//    @Override
//    protected User autoMap(UserEntity userEntity) {
//        throw new RuntimeException();
////        return super.autoMap(userEntity);
//    }
//}
