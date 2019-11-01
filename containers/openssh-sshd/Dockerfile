FROM alpine

RUN apk add --no-cache openssh-server openssh-sftp-server

COPY etc/ /etc/

ADD startup.sh /bin/

RUN chmod +x /bin/startup.sh && chmod 0600 /etc/ssh/ssh_host_*

EXPOSE 22

CMD ["/bin/startup.sh"]
