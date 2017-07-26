for i in `ps ax|awk '/java DPPLOCALAppSvr.M/{print $1}'`; do
    kill -9 $i
done
