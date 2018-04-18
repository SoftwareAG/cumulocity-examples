FROM python:alpine3.6

COPY ./hello /app
WORKDIR /app
RUN pip install -r requirements
ENTRYPOINT ["python"]
CMD ["application.py"]