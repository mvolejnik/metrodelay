echo $*

cluster=$1

if [ -z "${cluster}" ];then
  >&2 echo missing cluster parameter
  exit
fi

shift

ansible-playbook init-o11y.yaml --limit localhost -i localhost, --extra-vars @.vault.yaml --extra-vars "cluster=${cluster}" $*
