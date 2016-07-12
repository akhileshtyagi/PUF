package intent_collection;

import intent_collection.IntentData;
import android.content.Intent;

interface IIntentCollectionService {
IntentData get_intent_data();
void add_intent(Intent intent, int sender, int receiver);
}