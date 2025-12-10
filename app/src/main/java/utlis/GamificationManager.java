package com.example.finalproject.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GamificationManager {

    public static void addPoints(int pointsToAdd) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user.getUid());

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);

            // Get old points
            Long oldPoints = snapshot.getLong("currentPoints");
            if (oldPoints == null) oldPoints = 0L;

            // Calculate new points and level
            long newPoints = oldPoints + pointsToAdd;
            long newLevel = (newPoints / 100) + 1; // Example: Level up every 100 points

            String levelName = "Novice";
            if (newLevel > 5) levelName = "Achiever";
            if (newLevel > 10) levelName = "Master";

            // Update Firestore
            transaction.update(userRef, "currentPoints", newPoints);
            transaction.update(userRef, "level", newLevel);
            transaction.update(userRef, "levelName", levelName);

            return newPoints;
        });
    }
    // ... existing code ...

    // NEW: Update total focus time in Firestore
    public static void addFocusTime(long minutes) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user.getUid());

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);

            // Get old total time
            Long oldTime = snapshot.getLong("totalFocusMinutes");
            if (oldTime == null) oldTime = 0L;

            long newTime = oldTime + minutes;

            // Update Firestore
            transaction.update(userRef, "totalFocusMinutes", newTime);

            return newTime;
        });
    }
}
