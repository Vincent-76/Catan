FROM hseeberger/scala-sbt:16.0.1_1.5.4_2.13.6
RUN apt-get update && apt-get install -y libxrender1 libxtst6 libxi6 libgl1-mesa-glx libgtk-3-0
WORKDIR /Catan
ADD . /Catan
RUN chmod +x /Catan/run.sh
#RUN sbt compile && sbt test
ENTRYPOINT ["/Catan/run.sh"]
CMD [""]


#docker build -t vince76/catan .
#docker run -it vince76/catan
#docker run -it --rm -e DISPLAY=host.docker.internal:0.0 vince76/catan