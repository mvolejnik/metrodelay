export VULTR_API_KEY=$(ansible-vault view .vault.yaml --vault-password-file .vault.password | head -2 | grep api_key | awk '{print $2}' | tr -d '"')

cluster=$1
shift

if [ -z "$cluster" ]; then
  >&2 echo missing 'cluster' parameter blue/green
  exit
fi

ansible-playbook all.yaml --limit "g_o11y:&g_${cluster}" -i vultr.yaml --extra-vars "cluster=${cluster}" --extra-vars @.vault.yaml $@
