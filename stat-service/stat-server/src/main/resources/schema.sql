CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR NOT NULL,
    uri VARCHAR NOT NULL,
    requester_ip VARCHAR NOT NULL,
    request_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id)
);