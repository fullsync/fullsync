#!/bin/sh -e

USERNAME=${SFTP_USER_NAME:-default}
PASSWORD=${SFTP_USER_PASS:-default}

mkdir -p "/home/${USERNAME}" >/dev/null

adduser --no-create-home --disabled-password --gecos "" --home "/home/${USERNAME}" "${USERNAME}" >/dev/null

echo "${USERNAME}:${PASSWORD}" | chpasswd >/dev/null

exec /usr/sbin/sshd -e -D -p 22
