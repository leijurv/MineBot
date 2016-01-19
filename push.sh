if [ $# -lt 1 ]; then
  echo "You need to provide a commit message."
  exit
fi
if [ $# -gt 1 ]; then
  echo "Provide exactly one argument. "
  exit
fi
git pull
find . -name .DS_Store -print0 | xargs -0 rm -f --
git add --all .
git commit -m "$1"
git push
echo "------------"
echo "Commit ID: "
git rev-parse HEAD
echo "-------------"
time=$(date +"%r")
echo "Current time: $time"
