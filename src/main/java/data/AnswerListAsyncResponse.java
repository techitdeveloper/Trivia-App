package data;

import java.util.ArrayList;

import model.Question;

public interface AnswerListAsyncResponse {
    void processFinished(ArrayList<Question> questionArrayList);
}
