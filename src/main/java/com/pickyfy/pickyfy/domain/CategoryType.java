package com.pickyfy.pickyfy.domain;

public enum CategoryType {

    ALL("전체"),
    RESTAURANT("음식점"),
    CAFE_BAKERY("카페/베이커리"),
    BAR_PUB("바/펍"),
    BOOK_STATIONERY("도서/문구"),
    CULTURE("문화"),
    ETC("기타");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // 화면에 표시되는 이름으로 enum 찾기
    public static CategoryType fromDisplayName(String displayName) {
        for (CategoryType type : values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown category display name: " + displayName);
    }

    // enum 이름을 소문자로 변환 (URL 경로나 API 요청에서 사용)
    public String toLowerCaseString() {
        return this.name().toLowerCase();
    }
}
