package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models;

public class ModelChatUser {
    String uid;
    String name;
    String email;
    String imageUri;

    public ModelChatUser() {

    }

    public ModelChatUser(String uid, String name, String email, String imageUri) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageUri = imageUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
