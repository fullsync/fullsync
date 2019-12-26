FROM php:7.4-apache

RUN mv "$PHP_INI_DIR/php.ini-production" "$PHP_INI_DIR/php.ini"

COPY . /var/www/html

EXPOSE 80

# run with:
# docker build -t fullsync-sourceforge-io .
# docker run --rm -p 80:80 --name fullsync.sourceforge.io fullsync-sourceforge-io:latest
# then browse to http://localhost/
# or for development:
# docker run --rm -p 80:80 -v $(pwd):/var/www/html --name fullsync.sourceforge.io php:7.4-apache
