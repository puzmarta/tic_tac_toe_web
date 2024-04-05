FROM busybox:1.35

RUN adduser -D user
USER user
WORKDIR /home/user

COPY src/ .

ENTRYPOINT ["busybox"]
CMD ["httpd", "-f", "-v", "-p", "3000"]