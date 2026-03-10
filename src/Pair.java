public class Pair {

    private String word;
    private int editDistance;

    public Pair(String word, int editDistance) {
        this.word = word;
        this.editDistance = editDistance;
    }

    public String getWord() {
        return word;
    }

    public int getEditDistance() {
        return editDistance;
    }
}
