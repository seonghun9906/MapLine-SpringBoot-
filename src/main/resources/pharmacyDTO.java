public class SimplePharmacyDTO {
    private List<SimpleDocument> documents;
    private SimpleMeta meta;

    public List<SimpleDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<SimpleDocument> documents) {
        this.documents = documents;
    }

    public SimpleMeta getMeta() {
        return meta;
    }

    public void setMeta(SimpleMeta meta) {
        this.meta = meta;
    }

    public static class SimpleDocument {
        private String phone;
        private String placeName;
        private double x;
        private double y;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPlaceName() {
            return placeName;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }

        public String getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

    public static class SimpleMeta {
        private boolean isEnd;
        private int pageableCount;
        // 다른 필요한 필드들을 추가할 수 있습니다.

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public int getPageableCount() {
            return pageableCount;
        }

        public void setPageableCount(int pageableCount) {
            this.pageableCount = pageableCount;
        }
        // 다른 필요한 필드들에 대한 getter, setter를 추가할 수 있습니다.
    }
}