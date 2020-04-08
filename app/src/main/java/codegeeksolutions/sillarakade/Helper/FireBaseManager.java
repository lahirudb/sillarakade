package codegeeksolutions.sillarakade.Helper;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseManager {

    public DocumentReference getConnectToDocument(String collectionName, String documentName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docref = db.collection(collectionName)
                .document(documentName);

        return docref;
    }

    public CollectionReference saveData(String collection) {
        FirebaseFirestore dbr = FirebaseFirestore.getInstance();
        return dbr.collection(collection);
    }

}
