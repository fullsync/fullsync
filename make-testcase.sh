#!/bin/bash

if [ -z "$1" ]; then
	echo "usage: $0 <target directory>"
	exit 1
fi

BASE_DIR=`readlink -f "$1"`

SRC_DIR="${BASE_DIR}/source"
DST_DIR="${BASE_DIR}/target"

mkdir -p "${SRC_DIR}"
mkdir -p "${DST_DIR}"

#files in source
touch "${SRC_DIR}/new file in source.txt"
touch "${SRC_DIR}/some file.txt"                                  # same file in both directories
echo "old content" > "${SRC_DIR}/old in source new in target.txt" # file in source older than in target
touch --date "" "${SRC_DIR}/old in source new in target.txt"

#files in target
touch "${DST_DIR}/new file in target.txt"
cp -a "${SRC_DIR}/some file.txt" "${DST_DIR}/"
echo "new content" > "${DST_DIR}/old in source new in target.txt" # change content
echo "old content" > "${DST_DIR}/old in target new in source.txt" # file in target older than in source
touch --date "" "${DST_DIR}/old in target new in source.txt"

echo "new content" > "${SRC_DIR}/old in target new in source.txt" # and same file with newer content in src than in target
