-- Utilisateurs
CREATE TABLE users (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT    NOT NULL UNIQUE,
    password TEXT    NOT NULL,
    status   TEXT    NOT NULL DEFAULT 'offline'
);

-- Demandes d'amitié et relations
CREATE TABLE friendships (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id   INTEGER NOT NULL REFERENCES users(id),
    receiver_id INTEGER NOT NULL REFERENCES users(id),
    status      TEXT    NOT NULL DEFAULT 'pending',
    created_at  TEXT    NOT NULL DEFAULT (datetime('now')),
    UNIQUE(sender_id, receiver_id)
);

-- Messages privés et de groupe
CREATE TABLE messages (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id   INTEGER NOT NULL REFERENCES users(id),
    receiver_id INTEGER REFERENCES users(id),
    group_id    INTEGER REFERENCES groups(id),
    content     TEXT    NOT NULL,
    sent_at     TEXT    NOT NULL DEFAULT (datetime('now'))
);

-- Groupes
CREATE TABLE groups (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT    NOT NULL,
    created_by INTEGER NOT NULL REFERENCES users(id),
    created_at TEXT    NOT NULL DEFAULT (datetime('now'))
);

-- Membres d'un groupe
CREATE TABLE group_members (
    group_id  INTEGER NOT NULL REFERENCES groups(id),
    user_id   INTEGER NOT NULL REFERENCES users(id),
    PRIMARY KEY (group_id, user_id)
);

-- Notifications
CREATE TABLE notifications (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER NOT NULL REFERENCES users(id),
    type       TEXT    NOT NULL,
    content    TEXT    NOT NULL,
    is_read    INTEGER NOT NULL DEFAULT 0,
    created_at TEXT    NOT NULL DEFAULT (datetime('now'))
);
