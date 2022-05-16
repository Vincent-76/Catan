FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
RUN apt-get update && apt-get install -y libxrender1 libxtst6 libxi6 libgtk-3-0
WORKDIR /Catan
ADD . /Catan
RUN chmod +x /Catan/run.sh
#RUN sbt compile && sbt test
#ENTRYPOINT ["/Catan/run.sh"]
CMD ["sbt", "run"]


#docker build -t vince76/catan .
#docker run -it vince76/catan
#docker run -it --rm -e DISPLAY=host.docker.internal:0.0 vince76/catan


