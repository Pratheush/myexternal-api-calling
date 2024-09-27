package org.dailycodebuffer.codebufferspringbootmongodb.collection;

public record Comment(Integer postId, Integer id, String name, String email, String body) {
}
