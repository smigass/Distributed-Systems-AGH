from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.responses import JSONResponse


app = FastAPI()

    
class Vote(BaseModel):
    pool_id: str
    option_id: str
    voter_id: int
    
class PoolOption(BaseModel):
    id: str
    name: str
    votes: list[Vote]

class Pool(BaseModel):
    id: str
    name: str
    options: list[PoolOption]
    
pools = {
    
}
    
@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.post("/pools")
async def create_pool(name: str, options: list[str]):
    pool_options = [PoolOption(id=str(i), name=option, votes=[]) for i, option in enumerate(options)]
    pool = Pool(id=str(len(pools)), name=name, options=pool_options)
    pools[pool.id] = pool
    return pool

@app.get ("/pools")
async def get_pools():
    return [
        {
            "id": pool.id,
            "name": pool.name,
            "options": [
                {
                    "id": option.id,
                    "name": option.name,
                    "votes": len(option.votes)
                }
                for option in pool.options
            ]
        }
        for pool in pools.values()
    ]

@app.get("/pools/{pool_id}")
async def get_pool(pool_id: str):
    if pool_id in pools:
        pool = pools[pool_id]
        return {
            "id": pool.id,
            "name": pool.name,
            "options": [
                {
                    "id": option.id,
                    "name": option.name,
                    "votes": len(option.votes)
                }
                for option in pool.options
            ]
        }
    return JSONResponse(status_code=404, content={"message": "Pool not found"})

@app.delete("/vote")
async def withdraw_vote(pool_id: str, voter_id: int):
    pool = pools.get(pool_id)
    if pool is None:
        return JSONResponse(status_code=404, content={"message": "Pool not found"})
    option = None
    for o in pool.options:
        for vote in o.votes:
            if vote.voter_id == voter_id:
                option = o
    if option is None:
        return JSONResponse(status_code=404, content={"message": "Option not found"})
    for vote in option.votes:
        if vote.voter_id == voter_id:
            option.votes.remove(vote)
            return JSONResponse(status_code=200, content={"message": "Withdrawn vote"})

@app.put("/vote/{pool_id}")
async def change_vote(pool_id: str, option_id: str, voter_id: int):
    pool = pools.get(pool_id)
    if pool is None:
        return JSONResponse(status_code=404, content={"message": "Pool not found"})
    option = next((option for option in pool.options if option.id == option_id), None)
    if option is None:
        return JSONResponse(status_code=404, content={"message": "Option not found"})
    await withdraw_vote(pool_id=pool_id, voter_id=voter_id)
    await vote(pool_id=pool_id, option_id=option_id, voter_id=voter_id)
    return JSONResponse(status_code=200, content={"message": "Changed vote"})

@app.post("/vote/{pool_id}/{option_id}")
async def vote(pool_id: str, option_id: str, voter_id: int):
    pool = pools.get(pool_id)
    if pool:
        option = next((option for option in pool.options if option.id == option_id), None)
        if option is None:
            return JSONResponse(status_code=404, content={"message": "Option not found"})
        else:
            vote = Vote(pool_id=pool_id, option_id=option_id, voter_id=voter_id)
            if any(v.voter_id == voter_id for v in option.votes):
                return JSONResponse(status_code=400, content={"message": "Voter has already voted for this option"})
            option.votes.append(vote)
            return {
                "status": "success",
                "message": f"Vote cast for option {option_id} in pool {pool_id}",
                "Total votes": len(option.votes)
            }
    else:
        return JSONResponse(status_code=404, content={"message": "Pool or option not found"})
