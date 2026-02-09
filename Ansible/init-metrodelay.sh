echo $*

cluster=$1

if [ -z "${cluster}" ];then
  >&2 echo missing cluster parameter
  exit 1
fi

shift

ansible-playbook init-metrodelay.yaml -i localhost, --extra-vars @.vault.yaml --extra-vars "cluster=${cluster}" $*
