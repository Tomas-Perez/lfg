package employeeExample;

public class UserInfoManager {

    public static void main(String[] args) {
        new UserInfoManager();
    }

    public UserInfoManager(){
        System.out.println(UserInfoManager.class
                .getResource("/java/src/User.hbm.xml"));

        System.out.println(ClassLoader.getSystemClassLoader().getResource(
                "java/src/UserInfo.hbm.xml"));
    }
}
